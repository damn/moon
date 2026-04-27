(ns moon.txs)

; TODO no protocol, just data & functions

(defprotocol TransactionHandler
  (handle! [_ txs]))
