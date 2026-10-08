(ns editor.ui.widget.string
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextField TextTooltip)))

(defn value [widget]
  (.getText ^TextField widget))

(defn create [state schema v]
  (let [{:keys [skin]} @state]
    (doto (TextField. ^String (str v) skin)
      (.addListener (TextTooltip. ^String (str schema) skin)))))
