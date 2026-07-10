(ns clojure.editor.create-widget-s-string
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.editor.create-widget :refer [create-widget]]
            [com.badlogic.gdx.scenes.scene2d.ui.text-field :as text-field]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [clojure.tooltip :as tooltip]))

(defmethod create-widget :s/string
  [schema v {:keys [ctx/skin]}]
  (doto (text-field/new (str v) skin)
    (actor/addListener (text-tooltip/new (str schema) skin))))
