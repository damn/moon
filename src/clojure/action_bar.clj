(ns clojure.action-bar
  (:require [clojure.button-group :as button-group]
            [clojure.actor :as actor]
            [clojure.horizontal-group :as horizontal-group]
            [clojure.layout :as layout]
            [clojure.ui-table :as table]))

(defn create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (horizontal-group/new)
                                  (horizontal-group/space! 2)
                                  (horizontal-group/pad! 2)
                                  (actor/set-name! "moon.ui.action-bar.horizontal-group")
                                  (actor/set-user-object! (doto (button-group/new)
                                                       (button-group/set-max-check-count! 1)
                                                       (button-group/set-min-check-count! 0))))
                         :expand? true
                         :bottom? true}]]})
    (layout/set-fill-parent! true)
    (actor/set-name! "moon.ui.action-bar")))
