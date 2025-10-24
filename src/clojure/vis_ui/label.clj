(ns clojure.vis-ui.label
  (:require [cdq.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label)))

(defn create [text #_skin]
  (Label. ^CharSequence (str text) ui/skin))
