(ns clojure.ctx.add-stage-actors
  (:require [clojure.stage.add-actor :refer [add-actor!]]))

(defn f!
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (add-actor! stage (apply f ctx params))))
