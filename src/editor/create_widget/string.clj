(ns editor.create-widget.string
  (:require [clojure.gdx :as gdx]
            [scene2d.ui.text-field :as text-field]
            [scene2d.ui.text-tooltip :as text-tooltip]))

(defn f
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (gdx/add-listener! (text-tooltip/create (str schema) skin))))
