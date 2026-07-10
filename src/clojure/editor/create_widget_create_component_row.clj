(ns clojure.editor.create-widget-create-component-row
  (:require [gdl.actor :as actor]
            [clojure.editor.create-widget-rebuild-editor-window :as rebuild-editor-window]
            [gdl.event :as event]
            [clojure.scene2d.group :as group]
            [clojure.k-label-text :as k-label-text]
            [clojure.ui-label :as label]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.scene2d.utils.change-listener :as change-listener]))

(defn create-component-row
  [{:keys [skin
           editor-widget
           display-remove-component-button?
           k
           table]}]
  [{:actor (table/create
            {:table/cell-defaults {:pad 2}
             :table/rows [[{:actor (when display-remove-component-button?
                                     (doto (text-button/create
                                            {:text "-"
                                             :skin skin})
                                       (actor/add-listener (change-listener/create
                                                                (fn [event _actor]
                                                                  (actor/remove-actor (first (filter (fn [actor]
                                                                                                          (and (actor/get-user-object actor)
                                                                                                               (= k ((actor/get-user-object actor) 0))))
                                                                                                        (group/get-children table))))
                                                                  (let [ctx (:stage/ctx (event/get-stage event))]
                                                                    (rebuild-editor-window/rebuild-editor-window! ctx)))))))
                            :left? true}
                           {:actor (label/create
                                    {:text (k-label-text/f k)
                                     :skin skin})}]]})
    :right? true}
   {:actor nil
    :pad-top 2
    :pad-bottom 2
    :fill-y? true
    :expand-y? true}
   {:actor editor-widget
    :left? true}])
