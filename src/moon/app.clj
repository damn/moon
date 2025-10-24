(ns moon.app)

(defprotocol Input
  (set-input-processor! [_ input-processor]))

(defprotocol Graphics
  (def-color! [_ name rgba])
  (sprite-batch [_]))
