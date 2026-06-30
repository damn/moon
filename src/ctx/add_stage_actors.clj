(ns ctx.add-stage-actors
  (:require [clojure.gdx :as gdx]))

(defn f!
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (gdx/add-actor! stage (apply f ctx params))))
