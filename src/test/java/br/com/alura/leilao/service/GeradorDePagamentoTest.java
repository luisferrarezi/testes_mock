package br.com.alura.leilao.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.Assert;

class GeradorDePagamentoTest {

	private GeradorDePagamento service;

	@Mock
	private PagamentoDao pagamentoDao;
	
	@Mock
	private Clock clock;
	
	@Captor
	private ArgumentCaptor<Pagamento> pagamentoCaptor;
	
	@BeforeEach
	public void beforeEach() {
		MockitoAnnotations.initMocks(this);		
		this.service = new GeradorDePagamento(pagamentoDao, clock);
	}
	
	@Test
	void criaPagamentoVencedorLeilaoTest() {
		Leilao leilao = leilao();
		Lance lance = leilao.getLances().get(0);
		
		LocalDate data = LocalDate.of(2023, 04, 25);
		Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
		Mockito.when(clock.instant()).thenReturn(instant);
		Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		
		service.gerarPagamento(lance);
		Mockito.verify(pagamentoDao).salvar(pagamentoCaptor.capture());
		
		Pagamento pagamento = pagamentoCaptor.getValue();
		Assert.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
		Assert.assertEquals(lance.getValor(), pagamento.getValor());
		Assert.assertEquals(lance.getUsuario(), pagamento.getUsuario());
		Assert.assertFalse(pagamento.getPago());
		Assert.assertEquals(leilao, pagamento.getLeilao());
	}
	
	private Leilao leilao() {
	    Leilao leilao = new Leilao("Celular",
	                    new BigDecimal("500"),
	                    new Usuario("Fulano"));
	    
	    Lance segundo = new Lance(new Usuario("Ciclano"),
	                    new BigDecimal("900"));
	    
	    leilao.propoe(segundo);

	    return leilao;
	}
}
