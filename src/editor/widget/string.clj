(ns editor.widget.string
  (:require [scene2d.actor.add-listener :refer [add-listener!]]
            [ui.text-field :as text-field]
            [ui.text-tooltip :as text-tooltip]))

(defn create
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (add-listener! (text-tooltip/create (str schema) skin))))

(defn value
  [_ widget _schemas]
  (text-field/text widget))
