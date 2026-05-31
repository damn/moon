(ns create.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [gdx.app :as app]))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/cursors (let [{:keys [data path-format]} (-> "config/cursors.edn" io/resource slurp edn/read-string)]
                            (update-vals data
                                         (fn [[path hotspot]]
                                           (app/new-cursor app
                                                           (format path-format path)
                                                           hotspot))))))
