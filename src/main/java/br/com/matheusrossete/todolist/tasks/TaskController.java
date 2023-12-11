package br.com.matheusrossete.todolist.tasks;

import br.com.matheusrossete.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser"); //pega o id do usuario em questao
        taskModel.setIdUser((UUID) idUser); // coloca esse id atraves do setIdUser, em forma de UUID

        var currentDate = LocalDateTime.now(); // pega a data atual
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){ // verifica se a data atual é após a data que foi colocada como inicio e fim na task
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início / data de término deve ser maior que a data atual"); //caso seja, vai dar um erro na tela, pois a data ja passou
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){ // verifica se a data inicial é depois da data final
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início deve ser menor que a data de término"); //caso seja, vai dar um erro na tela
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    
    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {     //Busca todas as tarefas de um ID de usuario especifico
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

    //método para fazer updates nas tasks
    @PutMapping("/{id}") // id é a pathvariable e o spring boot ira substituir
    public TaskModel update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) { //@PathVariable se comporta como uma rota variavel, ou seja, ela vai ser diferente eepenedndo do id colocado

        var task = this.taskRepository.findById(id).orElse(null);

        var idUser = request.getAttribute("idUser");

        if (task.getIdUser().equals("idUser")){

        }

        Utils.copyNonNullProperties(taskModel, task);
        
        return this.taskRepository.save(task);
    }


}

