(ns editor.map-widget-table.component-row
  (:require [clojure.actor.get-user-object :refer [get-user-object]]
            [clojure.actor.remove :refer [remove!]]
            [clojure.actor.add-listener :refer [add-listener!]]
            [clojure.event.get-stage :refer [get-stage]]
            [editor.map-widget-table.k-label-text :as k-label-text]
            [clojure.group.children :refer [children]]
            [clojure.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [clojure.ui.text-button :as text-button]
            [clojure.change-listener :as change-listener]))

(defn create
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
                                       (add-listener! (change-listener/create
                                                       (fn [event _actor]
                                                         (remove! (first (filter (fn [actor]
                                                                                   (and (get-user-object actor)
                                                                                        (= k ((get-user-object actor) 0))))
                                                                                 (children table))))
                                                         (let [ctx (:stage/ctx (get-stage event))]
                                                           ((:ctx/rebuild-editor-window! ctx) ctx)))))))
                            :left? true}
                           {:actor (label/create
                                    {:text (k-label-text/f k)
                                     :skin skin})}]]})
    :right? true}
   {:actor nil #_(com.kotcrab.vis.ui.widget.Separator. "vertical")
    :pad-top 2
    :pad-bottom 2
    :fill-y? true
    :expand-y? true}
   {:actor editor-widget
    :left? true}])
