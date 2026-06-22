(ns clojure.editor.map-widget-table.component-row
  (:require [gdl.get-user-object :refer [get-user-object]]
            [gdl.remove :refer [remove!]]
            [gdl.add-listener :refer [add-listener!]]
            [gdl.get-stage :refer [get-stage]]
            [clojure.editor.map-widget-table.k-label-text :as k-label-text]
            [gdl.group.children :refer [children]]
            [gdl.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [gdl.text-button :as text-button]
            [gdl.change-listener :as change-listener]))

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
