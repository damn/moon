(ns cdq.create.audio)

(defn step
  [{:keys [ctx/app] :as ctx}
   {:keys [impl config]}]
  (assoc ctx :ctx/audio (impl (.getAudio app) (.getFiles app) config)))
