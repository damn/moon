(ns editor.create-widget.string
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [scene2d.ui.text-field :as text-field]
            [scene2d.ui.text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))
