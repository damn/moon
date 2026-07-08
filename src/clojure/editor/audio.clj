(ns clojure.editor.audio)

(defn f [{:keys [ctx/app] :as ctx}]
  (assoc ctx :ctx/audio (.getAudio app)))
