(ns editor.ui.widget.number
  (:require [clojure.edn :as edn])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextField TextTooltip)))

(defn value [widget]
  (edn/read-string (.getText ^TextField widget)))

(defn create [state schema v]
  (let [{:keys [skin]} @state]
    (doto (TextField. ^String (pr-str v) skin)
      (.addListener (TextTooltip. ^String (str schema) skin)))))
