(ns cdq.create.graphics)

(defn step
  [{:keys [ctx/app] :as ctx}
   {:keys [impl config]}]
  (assoc ctx :ctx/graphics (impl app config)))
