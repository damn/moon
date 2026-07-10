(ns clojure.moon.action-bar-create
  (:require [gdl.scenes.scene2d.actor :as actor]
            [clojure.ui.button-group :as button-group]
            [gdl.scenes.scene2d.ui.horizontal-group :as horizontal-group]
            [clojure.set-fill-parent! :as set-fill-parent!]
            [clojure.ui-table :as table]))

(defn action-bar-create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/new)
                                  (horizontal-group/space! 2)
                                  (horizontal-group/pad! 2)
                                  (actor/set-name "moon.ui.action-bar.horizontal-group")
                                  (actor/set-user-object (doto (button-group/new)
                                                       (button-group/set-max-check-count! 1)
                                                       (button-group/set-min-check-count! 0))))
                         :expand? true
                         :bottom? true}]]})
    (set-fill-parent!/f true)
    (actor/set-name "moon.ui.action-bar")))
