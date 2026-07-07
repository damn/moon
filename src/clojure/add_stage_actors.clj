(ns clojure.add-stage-actors
  (:require [clojure.stage :as stage]))

(defn f!
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (stage/add-actor! stage (apply f ctx params))))
