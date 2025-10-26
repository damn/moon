(ns moon.ui.text-button
  (:require [cdq.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d.ui TextButton)))

(defn create [text]
  (TextButton. (str text) ui/skin))
