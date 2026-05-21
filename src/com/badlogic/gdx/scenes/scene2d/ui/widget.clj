(ns com.badlogic.gdx.scenes.scene2d.ui.widget
  (:require [clojure.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Widget)))

(defmethod actor/create :ui/widget
  [{:keys [draw!]}]
  (proxy [Widget] []
    (draw [batch parent-alpha]
      (draw! this batch parent-alpha))))
