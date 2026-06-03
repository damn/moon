(ns editor.widget.string
  (:require [editor.widget :as widget]
            [clojure.gdx.scene2d.actor.add-listener :refer [add-listener!]]
            [clojure.gdx.scene2d.ui.text-field :as text-field]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]))

(defmethod widget/create :s/string [schema v {:keys [ctx/skin]}]
  (doto (text-field/create (str v) skin)
    (add-listener! (text-tooltip/create (str schema) skin))))

(defmethod widget/value :s/string [_ widget _schemas]
  (text-field/text widget))
