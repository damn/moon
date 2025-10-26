(ns moon.ui.label
  (:require [moon.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label)))

(defn create [text #_skin]
  (Label. ^CharSequence (str text) ui/skin))
