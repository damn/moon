(ns moon.ui.text-field
  (:require [moon.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextField)))

(defn create [text]
  (TextField. (str text) ui/skin))

(def text TextField/.getText)
