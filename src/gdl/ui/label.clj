(ns gdl.ui.label
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defn create [text ^Skin skin]
  (Label. ^CharSequence (str text) skin))

(defn set-text! [^Label label text]
  (.setText label (str text)))

(defn set-alignment! [^Label label align]
  (.setAlignment label align))
