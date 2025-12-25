(ns moon.application.create.audio)

(defn step
  [{:keys [ctx/audio
           ctx/files]
    :as ctx}
   {:keys [impl config]}]
  (assoc ctx :ctx/audio (impl audio files config)))
