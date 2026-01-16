(ns moon.create.add-stage-actors)

(defn step
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [actor (map (fn [[f & params]] (apply f ctx params)) actor-fns)]
    (.addActor stage actor))
  ctx)
