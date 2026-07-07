(ns editor.create-widget.string
  (:require [clojure.actor :as actor]
            [gdx.scene2d.ui.text-field :as text-field]
            [gdx.scene2d.ui.text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (actor/add-listener! (text-tooltip/create (str schema) skin))))
