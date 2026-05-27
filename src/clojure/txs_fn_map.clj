(ns clojure.txs-fn-map)

(defn actions!
  [txs-fn-map ctx txs]
  (loop [ctx ctx
         txs txs
         handled-txs []]
    (if (empty? txs)
      handled-txs
      (let [[k & params :as tx] (first txs)]
        (if tx
          (let [_ (assert (vector? tx))
                f (get txs-fn-map k)
                _ (assert f (str "Cannot find function for tx: " k))
                new-txs (try
                         (apply f ctx params)
                         (catch Throwable t
                           (throw (ex-info "Error handling tx"
                                           {:tx tx}
                                           t))))]

            ; TODO VALID RETURNS -> check!
            ; either nil? or vector of transactions (vectors = vector with keyword & params?)
            (recur ctx
                   (concat (or new-txs []) (rest txs))
                   (conj handled-txs tx)))
          (recur ctx
                 (rest txs)
                 handled-txs))))))

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
