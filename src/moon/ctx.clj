(ns moon.ctx
  (:require [moon.tx-handler :as tx-handler]))

(declare reaction-txs-fn-map
         txs-fn-map)

(defn reduce-actions!
  [txs-fn-map ctx txs]
  (loop [ctx ctx
         txs txs]
    (if (empty? txs)
      ctx
      (let [[k & params :as tx] (first txs)]
        (if tx
          (let [_ (assert (vector? tx))
                f (get txs-fn-map k)
                new-ctx (try
                         (if (nil? f)
                           ctx
                           (apply f ctx params))
                         (catch Throwable t
                           (throw (ex-info "Error handling tx"
                                           {:tx tx}
                                           t))))]
            (recur new-ctx
                   (rest txs)))
          (recur ctx
                 (rest txs)))))))

(defn handle! [ctx txs]
  (let [handled-txs (try (tx-handler/actions! txs-fn-map ctx txs)
                         (catch Throwable t
                           (throw (ex-info "Error handling txs"
                                           {:txs txs} t))))]
    (reduce-actions! reaction-txs-fn-map
                     ctx
                     handled-txs)))
