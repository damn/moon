(ns editor.map-widget-table.component-row
  (:require
            [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [clojure.gdx.group.get-children :as get-children]
            [editor.map-widget-table.k-label-text :as k-label-text]
            [scene2d.ui.label :as label]
            [gdx.scenes.scene2d.ui.table :as table]
            [scene2d.ui.text-button :as text-button]
            [scene2d.utils.change-listener :as change-listener]))

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
                                       (actor/add-listener! (change-listener/create
                                                        (fn [event _actor]
                                                          (actor/remove! (first (filter (fn [actor]
                                                                                      (and (actor/get-user-object actor)
                                                                                           (= k ((actor/get-user-object actor) 0))))
                                                                                    (get-children/f table))))
                                                          (let [ctx (:stage/ctx (event/get-stage event))]
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
