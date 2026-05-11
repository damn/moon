(ns moon.ui.impl.horizontal-group
  (:require [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui HorizontalGroup)))

(defmethod actor/create :ui/horizontal-group
  [{:keys [space pad] :as opts}]
  (doto (HorizontalGroup.)
    (.space space)
    (.pad pad)
    (actor/set-opts! opts)))
