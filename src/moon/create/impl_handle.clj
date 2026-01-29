(ns moon.create.impl-handle
  (:require [moon.ctx :as ctx]))

(defn- actions!
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
            (recur ctx
                   (concat (or new-txs []) (rest txs))
                   (conj handled-txs tx)))
          (recur ctx
                 (rest txs)
                 handled-txs))))))

(defn- reduce-actions!
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

(defn step
  [ctx
   {:keys [txs-fn-map
           reaction-txs-fn-map]}]
  (extend-type (class ctx)
    ctx/TransactionHandler
    (handle! [ctx txs]
      (let [handled-txs (try (actions! txs-fn-map ctx txs)
                             (catch Throwable t
                               (throw (ex-info "Error handling txs"
                                               {:txs txs} t))))]
        (reduce-actions! reaction-txs-fn-map
                         ctx
                         handled-txs))))
  ctx)
