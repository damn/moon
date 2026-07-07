(ns clojure.ctx-do
  (:require [clojure.actions :refer [actions!]]
            [clojure.tx :as tx]))

(defn do!
  [ctx txs]
  (try (actions! tx/f ctx txs)
       (catch Throwable t
         (throw (ex-info "Error handling txs"
                         {:txs txs} t)))))
