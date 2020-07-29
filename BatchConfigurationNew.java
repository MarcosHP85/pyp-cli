package ar.nasa;

import java.io.IOException;
import java.io.Writer;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.CsvLineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	public static final String QUERY = "SELECT aso.WO_NO,aso.STATE,aso.WO_NO_MADRE,aso.REAL_F_DATE,aso.CONTRACT,aso.SUBESTADO_PLANIF,aso.COM_INT_PLA,aso.PLAN_F_DATE,aso.PLAN_S_DATE,aso.MCH_CODE,aso.ORG_CODE,aso.TIPO_PARADA,aso.WORK_TYPE_ID,aso.PRIORITY_ID,aso.TAREA,aso.CATEGORY_ID,aso.REG_DATE,aso.FIRMA_PAQ_TRAB,aso.WORK_LEADER_SIGN,aso.CONFIRMACION_1,aso.PREPARED_BY,aso.COM_INT_PRO,aso.MCH_LOC,aso.MCH_POS,aso.COM_PLA_PRO,aso.COM_IMP_PLA,aso.COM_IMP_PRO,aso.REQ_PAQ_TAB,aso.PALABRA_CLAVE,aso.NOTAS_MOTIVO,aso.EQUIPOS_INDISPONIBLES,aso.REQUIERE_QC,aso.NOVEDAD_SEMANAL,aso.AGRUP_PROG,cep.semanas,cep.l1,cep.m1,cep.x1,cep.j1,cep.v1,cep.s1,cep.d1 " +
            "    FROM ifsata.ACTIVE_SEPARATE_OVERVIEW aso " +
            "    LEFT JOIN (SELECT wo_no,min(week_wo) week_wo,semanas,min(l1) l1,min(m1) m1,min(x1) x1,min(j1) j1,min(v1) v1,min(s1) s1,min(d1) d1 FROM ifsata.c_e8_e0_prog group by wo_no) cep " +
            "    ON aso.WO_NO = cep.WO_NO " +
            "    where aso.CONTRACT in (2000, 4000) and ( ( aso.WO_NO > 600000 AND ( not( aso.PLAN_S_DATE > (SYSDATE + 120) ) or ( aso.PLAN_S_DATE IS NULL ) ) ) or aso.WO_NO < 600000 ) " +
            "union " +
            "    SELECT WO_NO,'Cancelado' as STATE,WO_NO_MADRE,REAL_F_DATE,CONTRACT,SUBESTADO_PLANIF,COM_INT_PLA,PLAN_F_DATE,PLAN_S_DATE,MCH_CODE,ORG_CODE,TIPO_PARADA,WORK_TYPE_ID,PRIORITY_ID,TAREA,CATEGORY_ID,REG_DATE,FIRMA_PAQ_TRAB,WORK_LEADER_SIGN,CONFIRMACION_1,PREPARED_BY,COM_INT_PRO,MCH_LOC,MCH_POS,COM_PLA_PRO,COM_IMP_PLA,COM_IMP_PRO,REQ_PAQ_TAB,PALABRA_CLAVE,NOTAS_MOTIVO,EQUIPOS_INDISPONIBLES,REQUIERE_QC,NOVEDAD_SEMANAL,AGRUP_PROG,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL " +
            "    FROM ifsata.HISTORICAL_SEPARATE_OVERVIEW " +
            "    where CONTRACT in (2000, 4000) and WO_STATUS_ID = 'CANCELED' and REAL_F_DATE > add_months(SYSDATE, -12)";
    
	@Value("${out}")
	private String out;
	
	@Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;


    @Bean
    ResourcelessTransactionManager transactionManager() {
    	return new ResourcelessTransactionManager();
    }
    
    @Bean
    MapJobRepositoryFactoryBean mapJobRepository(ResourcelessTransactionManager transactionManager) throws Exception {
    	MapJobRepositoryFactoryBean jobRepository = new MapJobRepositoryFactoryBean();
    	jobRepository.setTransactionManager(transactionManager);
    	jobRepository.afterPropertiesSet();
    	
    	return jobRepository;
    }
    
    @Bean
    JobRepository jobRepository(MapJobRepositoryFactoryBean mapJobRepository) throws Exception {
    	return mapJobRepository.getObject();
    }
    
    @Bean
    public JobExplorer jobExplorer(MapJobRepositoryFactoryBean mapJobRepository) {
        return new SimpleJobExplorer(mapJobRepository.getJobInstanceDao(), mapJobRepository.getJobExecutionDao(),
        		mapJobRepository.getStepExecutionDao(), mapJobRepository.getExecutionContextDao());
    }
    
    @Bean
    SimpleJobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
    	SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        
        return launcher;
    }

    
    // tag::readerwriterprocessor[]
    @Bean
    public JdbcCursorItemReader<Ot> reader() {
    	JdbcCursorItemReader<Ot> reader = new JdbcCursorItemReader<Ot>();
    	
    	reader.setDataSource(dataSource);
    	reader.setSql(QUERY);
    	reader.setRowMapper(new OtRowMapper());
    	
    	return reader;
    }

    @Bean
    public FlatFileItemWriter<Ot> writer() {
    	FlatFileItemWriter<Ot> writer = new FlatFileItemWriter<Ot>();
    	FileSystemResource file = new FileSystemResource(out);
    	
    	writer.setResource(file);
    	writer.setEncoding("cp1252");
    	writer.setHeaderCallback(new FlatFileHeaderCallback(){
			@Override
			public void writeHeader(Writer writer) throws IOException {
				writer.write("Nº OT;Estado;OT Madre;Finalización Real;Planta OT;Subestado planif;Com int pla;Fin Prog;Inicio Prog;Componente;Org Mant;Tipo parada;Tipo Trabajo;Prioridad;Tarea;Clase Seg;Fecha Registro;Firma paq trab;Resp.Tarea;Confirmacion 1;Planifica;Com int pro;Recinto;Pos.en Tab.;Com pla pro;Com imp pla;Com imp pro;Requiere Paq Trabajo;Palabra clave;Notas Motivo;Equipos Indisponibles;Requiere qc;Novedad Semanal;Agrup prog;Semanas;L1;M1;X1;J1;V1;S1;D1");
			}
    	});

    	CsvLineAggregator<Ot> delLineAgg = new CsvLineAggregator<Ot>();
    	delLineAgg.setDelimiter(";");
    	
    	BeanWrapperFieldExtractor<Ot> fieldExtractor = new BeanWrapperFieldExtractor<Ot>();
    	fieldExtractor.setNames(new String[] {"ot", "estado", "otMadre", "finalizacionReal", "plantaOt", "subestadoPlanif", "comIntPla", "finProg", "inicioProg", "componente", "orgMant", "tipoParada", "tipoTrabajo", "prioridad", "tarea", "claseSeg", "fechaRegistro", "firmaPaqTrab", "respTarea", "confirmacion1", "planifica", "comIntPro", "recinto", "posEnTab", "comPlaPro", "comImpPla", "comImpPro", "requierePaqTrabajo", "palabraClave", "notasMotivo", "equiposIndisponibles", "requiereQc", "novedadSemanal", "agrupProg", "semanas", "l1", "m1", "x1", "j1", "v1", "s1", "d1"});
    	
    	delLineAgg.setFieldExtractor(fieldExtractor);
    	writer.setLineAggregator(delLineAgg);
    	
    	return writer;
    }
    
    // end::readerwriterprocessor[]

    // tag::listener[]

    @Bean
    public JobExecutionListener jobListener() {
        return new JobCompletionNotificationListener();
    }
    
    @Bean
    public StepExecutionListener stepListener() {
    	return new StepListener();
    }

    // end::listener[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(Step step, JobExecutionListener jobListener) {
        return jobBuilderFactory.get("bajadaIFS")
                .listener(jobListener)
                .flow(step)
                .end()
                .build();
    }
    
    @Bean
    public Step step(StepExecutionListener stepListener,
    		ItemReader<Ot> reader,
    		ItemWriter<Ot> writer) {
        return stepBuilderFactory.get("otsIFS")
        		.listener(stepListener)
                .<Ot, Ot> chunk(10000)
                .reader(reader)
                .writer(writer)
                .build();
    }
    // end::jobstep[]
}
