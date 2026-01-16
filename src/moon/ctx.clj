(ns moon.ctx)

(defprotocol TransactionHandler
  (handle! [_ txs]))

(defprotocol Graphics
  (draw! [_ draws]))
