(ns clojure.gdx.scene2d.ui.widget
  (:require [com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]
            [moon.ui.actor :as actor]))

(defmethod actor/create :ui/widget
  [opts]
  (widget/create opts))
