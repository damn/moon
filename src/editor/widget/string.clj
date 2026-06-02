(ns editor.widget.string
  (:require [editor.widget :as widget]
            [gdx.scenes.scene2d.ui.text-field :as text-field]
            [clojure.gdx.scene2d.ui.text-tooltip :as text-tooltip]))

(defmethod widget/create :s/string [schema v {:keys [ctx/skin]}]
  (text-field/create
   {:text (str v)
    :skin skin
    :actor/listeners [(text-tooltip/create (str schema) skin)]}))

(defmethod widget/value :s/string [_ widget _schemas]
  (text-field/text widget))
