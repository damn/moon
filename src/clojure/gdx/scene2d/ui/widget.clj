(ns clojure.gdx.scene2d.ui.widget
  (:require [com.badlogic.gdx.scenes.scene2d.ui.widget :as widget]
            [clojure.gdx.scene2d.actor :as actor]))

(defmethod actor/create :ui/widget
  [opts]
  (widget/create opts))
