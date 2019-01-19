package com.codecracker.service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.codecracker.vo.Update;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

@Service
public class UpdateService {

	Flux<Update> updates;
	
	DirectProcessor<Update> hotUpdate;
	
	Flux <String> tableFlux;
	
	Flux <String> asyncTableFlux;
	
	EmitterProcessor<String> processor;
	
	Function<Flux<String>, Flux<String>> convertToUpper =
			f -> f .map(String::toUpperCase);

	@PostConstruct
	public void createBroadCaster() {
		
		updates=Flux.interval(Duration.ofSeconds(1L))
		.map(element->{
			return new Update(element.toString(),UUID.randomUUID().toString());
		}).doOnNext(onNext->{
			print("Publishing new element with key "+onNext.getKey());
		});
		
		
		//HotSource
		hotUpdate=DirectProcessor.create();
		new Thread(() -> {
			int state=0;
			do {
				 print("generator with state "+state);
				 hotUpdate.onNext(new Update(Integer.toString(state),UUID.randomUUID().toString())); 
				 hotUpdate.delaySequence(Duration.ofSeconds(3));
				 try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * if (state == 1000) { hotUpdate.onComplete(); break; }
				 */ hotUpdate.doOnCancel(()->{
					 print("doOnCancel event on hot Source");
					 hotUpdate.onComplete();
				 });
			      state++;
			      print("Is Terminated "+hotUpdate.isTerminated());
			      if(hotUpdate.hasCompleted()) {
			    	  break;
			      }
			}while(!hotUpdate.isTerminated());
		}).start();

		
//		updates.subscribe();
		tableFlux=Flux.generate(()->{
			print("supply state");
			return 0;
		}, (state,sink)->{
			 print("generator with state "+state);
			 sink.next("3 x " + state + " = " + 3*state); 
		      if (state == 100) sink.complete(); 
		      return state + 1; 
		});
		
		
		asyncTableFlux=Flux.create((emitter)->{
			int state=0;
			while(true) {
				 print("generator with state "+state);
				 emitter.next("3 x " + state + " = " + 3*state); 
			      if (state == 100) {
			    	  emitter.complete();
			    	  break;
			      }
			      state++;
			}
		});
		
		
		//Processor example
		processor=EmitterProcessor.create();
		FluxSink<String> sink =processor.sink();
		int state=0;
		while(true) {
			 print("generator with state "+state);
			 sink.next("3 x " + state + " = " + 3*state); 
		      if (state == 100) {
		    	  sink.complete();
		    	  break;
		      }
		      state++;
		}
		
	}
	
	public Flux<Update> getStreamOfData(){
		return updates.doOnCancel(()->{
			print("doOnCancel");
		}).doOnComplete(()->{
			print("doOnComplete");
		}).doOnTerminate(()->{
			print("doOnTerminate");
		});
	}
	
	public Flux<String> getTable(){
		return tableFlux.doOnCancel(()->{
			print("doOnCancel");
		}).doOnComplete(()->{
			print("doOnComplete");
		}).doOnTerminate(()->{
			print("doOnTerminate");
		})
		.doOnNext(onNext->{
			print("onNext before publish on");
		})
		//.publishOn(Schedulers.newParallel("p", 10))
		.subscribeOn(Schedulers.newParallel("S", 10))
		;
	}

	public ParallelFlux<String> getTableParallel(){
		return tableFlux.doOnCancel(()->{
			print("doOnCancel");
		}).doOnComplete(()->{
			print("doOnComplete");
		}).doOnTerminate(()->{
			print("doOnTerminate");
		})
		.doOnNext(onNext->{
			print("onNext before publish on");
		})
		.parallel(10)
		.runOn(Schedulers.newParallel("PARALLEL", 10))
		//.publishOn(Schedulers.newParallel("p", 10))
		//.subscribeOn(Schedulers.newParallel("S", 10))
		;
	}

	
	public Flux<String> getAsyncTable(){
		return asyncTableFlux.doOnCancel(()->{
			print("doOnCancel");
		}).doOnComplete(()->{
			print("doOnComplete");
		}).doOnTerminate(()->{
			print("doOnTerminate");
		});
	}

	
	public Flux<String> getTableByProcessor(){
		return processor.doOnCancel(()->{
			print("doOnCancel");
		}).doOnComplete(()->{
			print("doOnComplete");
		}).doOnTerminate(()->{
			print("doOnTerminate");
		})
		.transform(convertToUpper);
	}

	
	public static void print(String message) {
		System.out.println("T:"+Thread.currentThread()+" "+message);
	}

	public Flux<Update> getDataStreamHotSource() {
		return hotUpdate.doOnCancel(()->{
			print("doOnCancel");
		}).doOnComplete(()->{
			print("doOnComplete");
		}).doOnTerminate(()->{
			print("doOnTerminate");
		});
	}
	
}
