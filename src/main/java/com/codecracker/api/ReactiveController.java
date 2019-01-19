package com.codecracker.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codecracker.service.UpdateService;
import com.codecracker.vo.Update;
import static com.codecracker.service.UpdateService.print; 

import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;

@Controller
@RequestMapping("/")
public class ReactiveController {

	@Autowired
	private UpdateService updateService;
	
	private int count=0;
	
	/**
	 * For example 1 Creating publisher which emits update every second.
	 * @return
	 */
	@RequestMapping(value="getDataStream",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<Update> getDataStream(){
		print("getDataStream");
		return updateService.getStreamOfData().doOnSubscribe(onSubscribe->{
			print("onSubscribe by user " );
		}).doOnNext(onNext->{
			print("onNext for user " );
		});
	}
	
	/**
	 * For example 2 Hot publisher example
	 * @return
	 */
	@RequestMapping(value="getDataStreamHotSource",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<Update> getDataStreamHotSource(){
		print("getDataStream");
		return updateService.getDataStreamHotSource().doOnSubscribe(onSubscribe->{
			print("onSubscribe by user " );
		}).doOnNext(onNext->{
			print("onNext for user " );
		});
	}

	/**
	 * 3. Flux.generate example
	 * @return
	 */
	@RequestMapping(value="getTable",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<String> getTable(){
		print("getTable");
		count=0;
		return updateService.getTable().doOnSubscribe(onSubscribe->{
			print("onSubscribe by user " );
		}).doOnNext(onNext->{
			print("onNext for user " );
			blockOn(3);
		});
	}

	
	/**
	 * 4. Flux.create example
	 * @return
	 */
	@RequestMapping(value="getAsyncTable",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<String> getAsyncTable(){
		print("getTable");
		count=0;
		return updateService.getAsyncTable().doOnSubscribe(onSubscribe->{
			print("onSubscribe by user " );
		}).doOnNext(onNext->{
			print("onNext for user " );
			blockOn(3);
			
		});
	}

	
	@RequestMapping(value="getTableParallel",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public ParallelFlux<String> getTableParallel(){
		print("getTable");
		count=0;
		return updateService.getTableParallel().doOnSubscribe(onSubscribe->{
			print("onSubscribe by user " );
		}).doOnNext(onNext->{
			print("onNext for user " );
			blockOn(3);
		});
	}

	
	@RequestMapping(value="getTableByProcessor",produces=MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<String> getTableByProcessor(){
		print("getTable");
		count=0;
		return updateService.getTableByProcessor().doOnSubscribe(onSubscribe->{
			print("onSubscribe by user " );
		}).doOnNext(onNext->{
			print("onNext for user " );
			blockOn(3);
			
		});
	}


	
	private void blockOn(int i) {
		count++;
		if(count==i) {
			try {
				print("Blocking");
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	@RequestMapping(value="ping")
	@ResponseBody
	public String ping(){
		print("ping");
		return "Hi There";
	}

}
