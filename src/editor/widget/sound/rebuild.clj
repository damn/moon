(ns editor.widget.sound.rebuild
  (:require [clojure.gdx :as gdx]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.ui.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (gdx/clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (gdx/remove! (find-ancestor actor gdx/window?))
    (gdx/pack! (find-ancestor table gdx/window?))
    (let [[k _] (gdx/get-user-object table)]
      (gdx/set-user-object! table [k sound-name]))))
