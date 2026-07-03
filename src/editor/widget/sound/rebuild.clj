(ns editor.widget.sound.rebuild
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.actor.remove :as remove]
            [clojure.gdx.actor.set-user-object :as set-user-object]
            [clojure.gdx.group.clear-children :as clear-children]
            [clojure.gdx.layout.pack :as pack]
            [clojure.gdx.window.instance? :as window?]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.ui.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children/f table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (remove/f (find-ancestor actor window?/f))
    (pack/f (find-ancestor table window?/f))
    (let [[k _] (get-user-object/f table)]
      (set-user-object/f table [k sound-name]))))
