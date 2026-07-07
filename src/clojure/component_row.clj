(ns clojure.component-row
  (:require
            [clojure.add-listener]
            [clojure.get-user-object]
            [clojure.remove-actor] [clojure.group :as group]
            [clojure.event :as event]
            [clojure.k-label-text :as k-label-text]
            [clojure.ui-label :as label]
            [clojure.ui-table :as table]
            [clojure.ui-text-button :as text-button]
            [clojure.utils-change-listener :as change-listener]))

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
                                       (clojure.add-listener/f (change-listener/create
                                                        (fn [event _actor]
                                                          (clojure.remove-actor/f (first (filter (fn [actor]
                                                                                      (and (clojure.get-user-object/f actor)
                                                                                           (= k ((clojure.get-user-object/f actor) 0))))
                                                                                    (group/get-children table))))
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
