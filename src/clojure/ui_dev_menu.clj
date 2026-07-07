(ns clojure.ui-dev-menu
  (:require [clojure.actor :as actor]
            [clojure.layout :as layout]
            [clojure.touchable :as touchable]
            [clojure.main-table :as main-table]
            [clojure.ui-label :as label]
            [clojure.ui-table :as table]))

(defn create
  [{:keys [menus update-labels skin]}]
  (doto (table/create
         {:table/rows [[{:actor (main-table/f skin menus update-labels)
                         :expand-x? true
                         :fill-x? true
                         :colspan 1}]
                       [{:actor (doto (label/create
                                       {:text ""
                                        :skin skin})
                                  (actor/set-touchable! touchable/disabled))
                         :expand? true
                         :fill-x? true
                         :fill-y? true}]]})
    (layout/set-fill-parent! true)))
