(ns ctx.tx.toggle-inventory-visible
  (:require [com.badlogic.gdx.scenes.scene2d.group :as group]
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]))

(defn f [{:keys [ctx/stage]}]
  (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
    (actor/set-visible! inventory (not (actor/visible? inventory)))
    nil))
