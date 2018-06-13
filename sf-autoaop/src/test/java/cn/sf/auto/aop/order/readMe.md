
aop0 order 0
aop1 order 1
aop2 order 2
######## no  exception  print 
aop0 begin
aop1 begin
aop2 begin
aop2 end
aop2 finally
aop1 end
aop1 finally
aop0 end
aop0 finally
########  exception  print 
aop0 begin
aop1 begin
aop2 begin
aop2 exception
aop2 finally
aop1 end
aop1 finally
aop0 end
aop0 finally
