(ns editor.create-widget.string
  (:require [clojure.gdx.actor.add-listener :as add-listener]
            [scene2d.ui.text-field :as text-field]
            [scene2d.ui.text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (add-listener/f (text-tooltip/create (str schema) skin))))
