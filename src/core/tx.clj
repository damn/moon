(ns core.tx
  (:require [core.component :refer [defsystem]]))

; 1. return new ctx if we change something in the ctx or have side effect -> will be recorded
; when returning a 'map?'

; 2. return seq of txs -> those txs will be done recursively
; 2.1 also seq of fns wih [ctx] param can be passed.

; 3. return nil in case of doing nothing -> will just continue with existing ctx.

; do NOT do a ctx/do! inside a effect/do! because then we have to return a context
; and that means that transaction will be recorded and done double with all the sub-transactions
; in the replay mode
; we only want to record actual side effects, not transactions returning other lower level transactions
(defsystem do! "FIXME" [_ ctx])
