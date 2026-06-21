(ns editor.widget.string
  (:require [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.scenes.scene2d.ui.text-field :as text-field]
            [clojure.scenes.scene2d.ui.text-tooltip :as text-tooltip]))

(defn create
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (add-listener! (text-tooltip/create (str schema) skin))))

(defn value
  [_ widget _schemas]
  (text-field/text widget))
