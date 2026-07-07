(ns clojure.ctx-do
  (:require [clojure.actions :refer [actions!]]
            [clojure.txs-fn-map :as txs-fn-map]))

(defn do!
  [ctx txs]
  (try (actions! txs-fn-map/f ctx txs)
       (catch Throwable t
         (throw (ex-info "Error handling txs"
                         {:txs txs} t)))))
