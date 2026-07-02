(ns editor.widget.sound.rebuild
  (:require [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.actor.remove :as remove]
            [clojure.gdx.actor.set-user-object :as set-user-object]
            [clojure.gdx.layout.pack :as pack]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.ui.table.add-rows :refer [add-rows!]])
  (:import (com.badlogic.gdx.scenes.scene2d Group)
           (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (Group/.clearChildren table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (remove/f (find-ancestor actor #(instance? Window %)))
    (pack/f (find-ancestor table #(instance? Window %)))
    (let [[k _] (get-user-object/f table)]
      (set-user-object/f table [k sound-name]))))
