(ns create.add-stage-actors
  (:require [clojure.gdx.scene2d.stage.add-actor :refer [add-actor!]]))

(defn step
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (add-actor! stage (apply f ctx params)))
  ctx)
